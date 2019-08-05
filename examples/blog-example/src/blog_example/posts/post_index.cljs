(ns blog-example.posts.post-index
  (:require [reagent.core :as r]
            [re-action.router :as router]
            [re-action.core :as re-action]
            [re-streamer.core :refer [subscribe unsubscribe]]
            [blog-example.posts.post-resource :as resource]))

;; === Presentational Components ===

(defn- post-index-header [search update-search]
  [:div {:class "text-center"}
   [:h2 "Posts"]
   [:input {:type        "text"
            :placeholder "Search"
            :class       "form-control search-input"
            :value       search
            :on-change   #(update-search (.. % -target -value))}]])

(defn- post-index-list [posts]
  [:div {:class "row buffer-top"}
   (for [post posts]
     [:div {:class "col-md-3 col-sm-4 buffer-bottom"
            :key   (:id post)}
      [:a {:class    "card"
           :on-click #(router/navigate (str "/posts/" (:id post)))}
       [:div {:class "card-body"}
        [:h5 {:class "card-title"} (:title post)]
        [:p {:class "card-text text-truncate"} (:body post)]]]])])

(defn- post-index-footer [page-sizes selected-size update-selected-size]
  [:div {:class "text-center"}
   (for [page-size page-sizes]
     [:button {:class    (str "btn mr-1 " (if (= page-size selected-size) "btn-primary" "btn-light"))
               :key      page-size
               :on-click (fn [_] (update-selected-size page-size))} page-size])])

;; === Facade ===

(defn- post-index-facade []
  (let [init-state {:posts [] :page-sizes [5 10 15 20] :selected-size 5 :search ""}
        store (re-action/store init-state)
        posts (re-action/select store :posts)
        page-sizes (re-action/select store :page-sizes)
        selected-size (re-action/select store :selected-size)
        search (re-action/select store :search)
        get-posts (re-action/select-distinct store :selected-size :search)]

    (subscribe get-posts #(re-action/patch-state! store {:posts (resource/get-posts %)}))

    {:posts                posts
     :page-sizes           page-sizes
     :selected-size        selected-size
     :search               search
     :update-selected-size #(re-action/patch-state! store {:selected-size %})
     :update-search        #(re-action/patch-state! store {:search %})}))

;; === Container Component ===

(defn post-index-container []
  (let [facade (post-index-facade)]
    (r/create-class {:reagent-render (fn []
                                       [:div
                                        [post-index-header @(-> facade :search :state) (:update-search facade)]
                                        [post-index-list @(-> facade :posts :state)]
                                        [post-index-footer @(-> facade :page-sizes :state)
                                         @(-> facade :selected-size :state) (:update-selected-size facade)]])})))
