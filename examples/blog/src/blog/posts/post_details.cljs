(ns blog.posts.post-details
  (:require [blog.posts.resource :as resource]
            [re-action.router :as router]))

;; === Facade ===

(defn- facade []
  {:get-post #(or (resource/get-post (js/parseInt %))
                  (router/navigate "/not-found"))})

;; === Container Component ===

(defn container [id]
  (let [facade (facade)
        post ((:get-post facade) id)]
    [:div {:class "card"}
     [:div {:class "card-header"} "Post Details"]
     [:div {:class "card-body"}
      [:label {:class "font-italic font-weight-bold"} "Title"]
      [:div {:class "card-text"} (:title post)]
      [:hr]
      [:label {:class "font-italic font-weight-bold"} "Body"]
      [:div {:class "card-text text-justify"} (:body post)]]]))
