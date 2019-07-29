(ns simple-routing-example.core
    (:require
      [reagent.core :as r]
      [re-action.routing :as routing]))

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

(routing/route "#/home" home-page)
(routing/route "#/about" about-page)
(routing/route "#/not-found" not-found-page)

(routing/redirect "" "#/home")
(routing/redirect "**" "#/not-found")

(routing/start-routing)

;; === App Container ===

(defn app []
  [:div
   [:h2 "App Header"]
   [:button {:on-click #(routing/navigate "")} "Home"]
   [:button {:on-click #(routing/navigate "#/about")} "About"]
   [:button {:on-click #(routing/navigate "#/not-defined-route")} "Not Defined Route"]
   [:br]
   [@routing/router-outlet]
   [:small "App Footer"]])

(defn mount-root []
  (r/render [app] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
