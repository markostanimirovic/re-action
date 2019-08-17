(ns blog.posts.post-edit
  (:require [blog.posts.resource :as resource]
            [re-action.core :as re-action]
            [re-action.form :as form]
            [re-action.router :as router]
            [re-streamer.core :as re-streamer :refer [subscribe]]))

;; === Presentational Components ===

(defn- edit-form []
  (let [post-form (form/create {:title {:required (comp not empty?)}
                                :body  {:required (comp not empty?)}})]
    (fn [post save]
      [:form {:on-submit     #(do (.preventDefault %)
                                  (save (form/value @post-form)))
              :auto-complete :off}
       [:div.form-group
        [:label {:for :title} "Title" [:span.text-danger " *"]]
        [:input.form-control {:id            :title
                              :type          :text
                              :default-value (:title post)
                              :class         (list
                                               (when (and (form/valid? @post-form :title)
                                                          (or (form/touched? @post-form :title)
                                                              (form/dirty? @post-form :title))) :is-valid)
                                               (when (and (not (form/valid? @post-form :title))
                                                          (or (form/touched? @post-form :title)
                                                              (form/dirty? @post-form :title))) :is-invalid))}]
        (when (and (not (form/valid? @post-form :title))
                   (or (form/touched? @post-form :title)
                       (form/dirty? @post-form :title)))
          [:div.invalid-feedback "Title is required field."])]
       [:div.form-group
        [:label {:for :password} "Body" [:span.text-danger " *"]]
        [:textarea.form-control {:id            :body
                                 :default-value (:body post)
                                 :rows          4
                                 :class         (list
                                                  (when (and (form/valid? @post-form :body)
                                                             (or (form/touched? @post-form :body)
                                                                 (form/dirty? @post-form :body))) :is-valid)
                                                  (when (and (not (form/valid? @post-form :body))
                                                             (or (form/touched? @post-form :body)
                                                                 (form/dirty? @post-form :body))) :is-invalid))}]
        (when (and (not (form/valid? @post-form :body))
                   (or (form/touched? @post-form :body)
                       (form/dirty? @post-form :body)))
          [:div.invalid-feedback {:key :required} "Body is required field."])]
       [:button.btn.btn-primary {:disabled (not (form/valid? @post-form))} "Save"]])))


;; === Facade ===

(defn- facade []
  (let [init-state {:post-id nil :post nil :edit-mode false}
        store (re-action/store init-state)
        post (re-action/select store :post)
        post-id (-> store
                    (re-action/select-distinct :post-id)
                    (re-streamer/skip 1))]

    (subscribe post-id (fn [post-id]
                         (let [post (resource/get-post (js/parseInt post-id))]
                           (if (nil? post)
                             (router/navigate "/not-found")
                             (re-action/patch-state! store {:post post})))))

    {:post           (:state post)
     :update-post-id #(re-action/patch-state! store {:post-id %})
     :save           #(do
                        (resource/update-post (into @(:state post) %))
                        (router/navigate (str "/posts/" @(:state post-id))))}))

;; === Container Component ===

(defn container []
  (let [facade (facade)]
    (fn [id]
      ((:update-post-id facade) id)
      [:div.row.justify-content-center
       [:div.col-md-9
        ^{:key id}
        [edit-form @(:post facade) (:save facade)]]])))
