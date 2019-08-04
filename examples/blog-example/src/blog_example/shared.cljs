(ns blog-example.shared
  (:require [goog.string :as gstring]
            [re-action.router :as router]))

(defn header []
  [:nav {:class "navbar navbar-expand navbar-light bg-light"}
   [:ul {:class "navbar-nav"}
    [:li {:class "nav-item"}
     [:a {:class    "nav-link"
          :on-click #(router/navigate "/")} "Home"]]
    [:li {:class "nav-item"}
     [:a {:class    "nav-link"
          :on-click #(router/navigate "/posts")} "Posts"]]]])

(defn footer []
  [:footer {:class "container footer"}
   [:hr]
   [:span {:class "text-muted"}
    (gstring/unescapeEntities "&copy;") " 2019 Marko Stanimirović"]])

(defn not-found [])