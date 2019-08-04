(ns blog-example.posts.post-index
  (:require [reagent.core :as r]
            [re-action.router :as router]
            [re-action.core :as re-action]
            [re-streamer.core :refer [subscribe unsubscribe]]
            [blog-example.posts.post-resource :as resource]))

(defn post-index-header [search update-search]
  [:div {:class "text-center"}
   [:h1 "Posts"]
   [:input {:type        "text"
            :placeholder "Search"
            :class       "form-control search-input"
            :value       search
            :on-change   #(update-search (.. % -target -value))}]])

(defn post-index-list [posts]
  [:div {:class "card-grid"}
   (for [post posts]
     [:a {:class    "card-grid-item"
          :key      (:id post)
          :on-click #(router/navigate (str "/posts/" (:id post)))}
      [:div {:class "card"}
       [:div {:class "card-body"}
        [:h5 {:class "card-title"} (:title post)]
        [:p {:class "card-text"} (:body post)]]]])])

(defn post-index-footer [page-sizes page-size update-page-size]
  [:div {:class "text-center"}
   (for [ps page-sizes]
     [:button {:class    (str "btn mr-1 " (if (= ps page-size) "btn-primary" "btn-light"))
               :key      ps
               :on-click (fn [_] (update-page-size ps))}
      ps])])

(defonce post-index-init-state {:posts      []
                                :page-sizes [5 10 15 20]
                                :page-size  5
                                :search     ""})

(defn post-index-facade []
  (let [store (re-action/store post-index-init-state)
        posts (re-action/select store :posts)
        page-sizes (re-action/select store :page-sizes)
        page-size (re-action/select store :page-size)
        search (re-action/select store :search)
        get-posts (re-action/select-distinct store :page-size :search)
        get-posts-sub (subscribe get-posts #(re-action/patch-state! store {:posts (resource/get-posts %)}))]

    {:posts            posts
     :page-sizes       page-sizes
     :page-size        page-size
     :search           search
     :update-page-size #(re-action/patch-state! store {:page-size %})
     :update-search    #(re-action/patch-state! store {:search %})
     :destroy          #(unsubscribe get-posts get-posts-sub)}))

(defn post-index-container []
  (let [facade (post-index-facade)]
    (r/create-class {:reagent-render (fn []
                                       [:div
                                        [post-index-header @(-> facade :search :state) (:update-search facade)]
                                        [post-index-list @(-> facade :posts :state)]
                                        [post-index-footer @(-> facade :page-sizes :state)
                                         @(-> facade :page-size :state) (:update-page-size facade)]])})))
