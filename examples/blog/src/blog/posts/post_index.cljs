(ns blog.posts.post-index
  (:require [blog.posts.resource :as resource]
            [re-action.router :as router]
            [re-action.core :as re-action]
            [re-streamer.core :refer [subscribe]]))

;; === Presentational Components ===

(defn- search [search update-search]
  [:div {:class "text-center"}
   [:h2 "Posts"]
   [:input {:type        "text"
            :placeholder "Search"
            :class       "form-control search-input"
            :value       search
            :on-change   #(update-search (.. % -target -value))}]])

(defn- posts-list [posts details]
  [:div {:class "row buffer-top"}
   (for [post posts]
     [:div {:class "col-md-3 col-sm-4 buffer-bottom"
            :key   (:id post)}
      [:a {:class    "card"
           :on-click (fn [_] (details post))}
       [:div {:class "card-body"}
        [:h5 {:class "card-title"} (:title post)]
        [:p {:class "card-text text-truncate"} (:body post)]]]])])

(defn- pagination [page-sizes selected-size update-selected-size]
  [:div {:class "text-center"}
   (for [page-size page-sizes]
     [:button {:class    (str "btn mr-1 " (if (= page-size selected-size) "btn-primary" "btn-light"))
               :key      page-size
               :on-click (fn [_] (update-selected-size page-size))} page-size])])

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
     :details              #(router/navigate (str "/posts/" (:id %)))}))

;; === Container Component ===

(defn container []
  (let [facade (facade)]
    (fn []
      [:div
       [search @(:search facade) (:update-search facade)]
       [posts-list @(:posts facade) (:details facade)]
       [pagination @(:page-sizes facade) @(:selected-size facade) (:update-selected-size facade)]])))
