(ns blog.shared
  (:require [goog.string :as gstring]
            [re-action.router :as router]))

(defn header []
  [:nav.navbar.navbar-expand.navbar-light.bg-light
   [:ul.navbar-nav
    [:li.nav-item
     [:a.nav-link {:on-click #(router/navigate "/")} "Home"]]
    [:li.nav-item
     [:a.nav-link {:on-click #(router/navigate "/posts")} "Posts"]]]])

(defn footer []
  [:footer.container.buffer-bottom.text-center
   [:hr]
   [:span.text-muted
    (gstring/unescapeEntities "&copy;") " 2019 Marko Stanimirović"]])

(defn shell []
  [:div.wrapper
   [header]
   [:div.container.body.buffer-top (router/outlet)]
   [footer]])

(defn home []
  [:div.text-center
   [:h1 "Welcome to Clojure Blog!"]
   [:img {:src "https://www.stickpng.com/assets/images/5847eb17cef1014c0b5e4849.png"}]])

(defn not-found []
  [:div.text-danger.text-center
   [:h1 "Oops!"]
   [:h2 "Something went wrong."]
   [:h3 "Take me " [:button.btn.btn-outline-danger.btn-lg {:on-click #(router/navigate "/home")} "home"]]])
