(ns simple-routing-example.core
    (:require
      [reagent.core :as r]
      [re-action.router :as router]))

;; === Pages ===

(defn home-page []
  [:div
   [:h3 "Home"]
   [:p "Welcome to home page!"]])

(defn about-page []
  [:div
   [:h3 "About"]
   [:p "This is about page"]])

(defn not-found-page []
  [:div
   [:h3 "404 Not Found"]
   [:p "Oops! Something went wrong"]])

;; === Routes ===

(router/defroute "/home" home-page)
(router/defroute "/about/me" about-page)
(router/defroute "/not-found" not-found-page)

(router/redirect "/" "/home")
(router/redirect "**" "/not-found")

(router/start)

;; === App Container ===

(defn app []
  [:div
   [:h2 "App Header"]
   [:button {:on-click #(router/navigate "/")} "Home"]
   [:button {:on-click #(router/navigate "/about/me")} "About"]
   [:button {:on-click #(router/navigate "/not-defined-route-123")} "Not Defined Route"]
   [:br]
   [@router/outlet]
   [:small "App Footer"]])

(defn mount-root []
  (r/render [app] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
