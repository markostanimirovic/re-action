(ns todo-routing-app.core
  (:require [reagent.core :as r]
            [re-action.router :as router]
            [re-action.session :as session]
            [re-streamer.core :refer [subscribe unsubscribe]]))

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

;; === Example: Get current route with segments and params from session ===

(defn todo-edit-page []
  (let [current-route (session/get :current-route)
        current-route-sub (subscribe current-route #(println %))]
    (r/create-class {:reagent-render         (fn []
                                               [:div
                                                [:h3 "TODO Edit"]
                                                [:p (str "Current Route Segments: " (:segments @(:state current-route)))]
                                                [:p (str "Current Route Params: " (:params @(:state current-route)))]])

                     :component-will-unmount #(unsubscribe current-route current-route-sub)})))

(defn not-found-page []
  [:div
   [:h3 "404 Not Found"]
   [:p "Oops! Something went wrong"]])

;; === Routes ===

(router/defroute "/home" home-page)
(router/defroute "/foo/bar" foo-bar-page)
(router/defroute "/todo/:id" todo-details-page)
(router/defroute "/todo/:id/edit" todo-edit-page)
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
   [:button {:on-click #(router/navigate "/todo/1")} "TODO 1 Details"]
   [:button {:on-click #(router/navigate "/todo/2")} "TODO 2 Details"]
   [:button {:on-click #(router/navigate "/todo/1/edit")} "TODO 1 Edit"]
   [:button {:on-click #(router/navigate "/todo/2/edit")} "TODO 2 Edit"]
   [:button {:on-click #(router/navigate "/not-defined-route-123")} "Not Defined Route"]
   [:br]
   (router/outlet)
   [:small "App Footer"]])

(defn mount-root []
  (r/render [app] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
