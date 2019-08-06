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
  [:footer {:class "container buffer-bottom text-center"}
   [:hr]
   [:span {:class "text-muted"}
    (gstring/unescapeEntities "&copy;") " 2019 Marko Stanimirović"]])

(defn shell []
  [:div {:class "wrapper"}
   [header]
   [:div {:class "container body buffer-top"} (router/outlet)]
   [footer]])

(defn home []
  [:div {:class "text-center"}
   [:h1 "Welcome to Clojure Blog!"]
   [:img {:src "https://www.stickpng.com/assets/images/5847eb17cef1014c0b5e4849.png"}]])

(defn not-found []
  [:div {:class "text-danger text-center"}
   [:h1 "Oops!"]
   [:h2 "Something went wrong."]
   [:h3 "Take me " [:button {:class    "btn btn-outline-danger btn-lg"
                             :on-click #(router/navigate "/home")} "home"]]])
