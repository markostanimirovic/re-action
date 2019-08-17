(ns blog.posts.post-edit
  (:require [blog.posts.resource :as resource]
            [re-action.core :as re-action]
            [re-action.form :as form]
            [re-action.router :as router]
            [re-streamer.core :as re-streamer :refer [subscribe]]))

;; === Presentational Components ===

(defn header [edit-mode back]
  [:div.card-header.page-header
   [:div.page-title [:h5 (str (if (true? edit-mode) "Edit " "Create ") "Post")]]
   [:div.page-actions
    [:span.action-button {:on-click #(back)}
     [:i.fas.fa-chevron-left]]]])

(defn body [post save]
  (let [post-form (form/create {:title {:required (comp not empty?)}
                                :body  {:required (comp not empty?)}})]
    (fn []
      [:div.card-body
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
        [:button.btn.btn-primary.float-right {:disabled (not (form/valid? @post-form))} "Save"]]])))

;; === Facade ===

(defn- facade []
  (let [init-state {:post-id nil :post nil :edit-mode false}
        store (re-action/store init-state)
        post (re-action/select store :post)
        edit-mode (re-action/select store :edit-mode)
        post-id (-> store
                    (re-action/select-distinct :post-id)
                    (re-streamer/skip 1))]

    (subscribe post-id (fn [post-id]
                         (if (nil? post-id)
                           (re-action/patch-state! store {:post nil :edit-mode false})
                           (let [post (resource/get-post (js/parseInt post-id))]
                             (if (nil? post)
                               (router/navigate "/not-found")
                               (re-action/patch-state! store {:post post :edit-mode true}))))))

    {:post           (:state post)
     :edit-mode      (:state edit-mode)
     :update-post-id #(re-action/patch-state! store {:post-id %})
     :save           #(let [post (if (true? @(:state edit-mode))
                                   (resource/update-post (into @(:state post) %))
                                   (resource/create-post %))]
                        (if (true? @(:state edit-mode))
                          (re-action/patch-state! store {:post post})
                          (router/navigate (str "/posts/" (:id post) "/edit"))))
     :back           #(router/navigate (str "/posts/" @(:state post-id)))}))

;; === Container Component ===

(defn container []
  (let [facade (facade)]
    (fn [id]
      ((:update-post-id facade) id)
      [:div.row.justify-content-center
       [:div.col-md-9
        [:div.card {:key id}
         [header @(:edit-mode facade) (:back facade)]
         [body @(:post facade) (:save facade)]]]])))
