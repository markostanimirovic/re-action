(ns blog.posts.post-index
  (:require [blog.posts.resource :as resource]
            [re-action.router :as router]
            [re-action.core :as re-action]
            [re-streamer.core :refer [subscribe]]))

;; === Presentational Components ===

(defn- header [search update-search create]
  [:div.card-header.page-header
   [:div.page-title [:h5 "Posts"]]
   [:div.page-search
    [:input.form-control {:type        :text
                          :placeholder "Search"
                          :value       search
                          :on-change   #(update-search (.. % -target -value))}]]
   [:div.page-actions
    [:span.action-button {:on-click #(create)} [:i.fas.fa-plus]]]])

(defn- body [posts details]
  [:div.card-body.row
   (for [post posts]
     [:div.col-md-3.col-sm-4.buffer-bottom {:key (:id post)}
      [:a.card {:on-click #(details post)}
       [:div.card-body
        [:h5.card-title (:title post)]
        [:p.card-text.text-truncate (:body post)]]]])])

(defn- footer [page-sizes selected-size update-selected-size]
  [:div.card-footer.text-center
   (for [page-size page-sizes]
     [:button.btn.mr-1 {:class    (if (= page-size selected-size) "btn-primary" "btn-light")
                        :key      page-size
                        :on-click #(update-selected-size page-size)} page-size])])

;; === Facade ===

(defn- facade []
  (let [init-state {:posts [] :page-sizes [5 10 15 20] :selected-size 5 :search ""}
        store (re-action/store init-state)
        posts (re-action/select store :posts)
        page-sizes (re-action/select store :page-sizes)
        selected-size (re-action/select store :selected-size)
        search (re-action/select store :search)
        get-posts (re-action/select-distinct store :selected-size :search)]

    (subscribe get-posts #(re-action/patch-state! store {:posts (resource/get-posts %)}))

    {:posts                (:state posts)
     :page-sizes           (:state page-sizes)
     :selected-size        (:state selected-size)
     :search               (:state search)
     :update-selected-size #(re-action/patch-state! store {:selected-size %})
     :update-search        #(re-action/patch-state! store {:search %})
     :details              #(router/navigate (str "/posts/" (:id %)))
     :create               #(router/navigate "/posts/create")}))

;; === Container Component ===

(defn container []
  (let [facade (facade)]
    (fn []
      [:div.card
       [header @(:search facade) (:update-search facade) (:create facade)]
       [body @(:posts facade) (:details facade)]
       [footer @(:page-sizes facade) @(:selected-size facade) (:update-selected-size facade)]])))
