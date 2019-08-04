(ns simple-routing-example.core
  (:require [reagent.core :as r]
            [re-action.router :as router]))

;; === Pages ===

(defn home-page []
  [:div
   [:h3 "Home"]
   [:p "Welcome to home page!"]])

(defn foo-bar-page []
  [:div
   [:h3 "Foo Bar"]
   [:p "This is foo bar page"]])

(defn todo-details-page [id]
  [:div
   [:h3 "TODO Details"]
   [:p (str "Body of TODO with id: " id)]])

(defn not-found-page []
  [:div
   [:h3 "404 Not Found"]
   [:p "Oops! Something went wrong"]])

;; === Routes ===

(router/defroute "/home" home-page)
(router/defroute "/foo/bar" foo-bar-page)
(router/defroute "/todo/:id" todo-details-page)
(router/defroute "/not-found" not-found-page)

(router/redirect "/" "/home")
(router/redirect "/foo-bar" "/foo/bar")
(router/redirect "**" "/not-found")

(router/start)

;; === App Container ===

(defn app []
  [:div
   [:h2 "App Header"]
   [:button {:on-click #(router/navigate "/")} "Home"]
   [:button {:on-click #(router/navigate "/foo/bar")} "Foo Bar"]
   [:button {:on-click #(router/navigate "/todo/1")} "TODO 1"]
   [:button {:on-click #(router/navigate "/todo/2")} "TODO 2"]
   [:button {:on-click #(router/navigate "/not-defined-route-123")} "Not Defined Route"]
   [:br]
   (router/outlet)
   [:small "App Footer"]])

(defn mount-root []
  (r/render [app] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
