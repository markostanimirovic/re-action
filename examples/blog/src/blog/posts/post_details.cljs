(ns blog.posts.post-details
  (:require [blog.posts.resource :as resource]
            [re-action.core :as re-action]
            [re-action.router :as router]
            [re-streamer.core :as re-streamer :refer [subscribe]]))

;; === Presentational Components ===

(defn card [post]
  [:div {:class "card"}
   [:div {:class "card-header"} "Post Details"]
   [:div {:class "card-body"}
    [:label {:class "font-italic font-weight-bold"} "Title"]
    [:div {:class "card-text"} (:title post)]
    [:hr]
    [:label {:class "font-italic font-weight-bold"} "Body"]
    [:div {:class "card-text text-justify"} (:body post)]]])

;; === Facade ===

(defn- facade []
  (let [init-state {:post-id nil :post nil}
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
     :update-post-id #(re-action/patch-state! store {:post-id %})}))

;; === Container Component ===

(defn container []
  (let [facade (facade)]
    (fn [id]
      ((:update-post-id facade) id)
      [card @(:post facade)])))
