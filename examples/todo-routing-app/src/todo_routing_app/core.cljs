(ns todo-routing-app.core
  (:require [re-action.router :as router]
            [re-action.session :as session]
            [reagent.core :as r]
            [re-streamer.core :refer [subscribe unsubscribe]]))

;; === Pages ===

(defn todo-home []
  [:div
   [:h3 "Home"]
   [:p "Welcome to TODO home page!"]])

(defn todo-list []
  [:div
   [:h3 "TODO List"]
   [:p "This is TODO list page"]])

(defn todo-details [id]
  [:div
   [:h3 "TODO Details"]
   [:p (str "Body of TODO with id: " id)]])

(defn todo-create []
  [:div
   [:h3 "TODO Create"]
   [:p "This is TODO create page"]])

;; === Example: Get current route with segments and params from session ===

(defn todo-edit []
  (let [current-route (session/get :current-route)
        current-route-sub (subscribe current-route #(println %))]
    (r/create-class {:reagent-render         (fn []
                                               [:div
                                                [:h3 "TODO Edit"]
                                                [:p (str "Current Route Segments: " (:segments @(:state current-route)))]
                                                [:p (str "Current Route Params: " (:params @(:state current-route)))]])

                     :component-will-unmount #(unsubscribe current-route current-route-sub)})))

(defn not-found []
  [:div
   [:h3 "404 Not Found"]
   [:p "Oops! Something went wrong!"]])

;; === Routes ===

(router/defroute "/home" todo-home)
(router/defroute "/todo/list" todo-list)
(router/defroute "/todo/:id" todo-details)
(router/defroute "/todo/create" todo-create)
(router/defroute "/todo/:id/edit" todo-edit)
(router/defroute "/not-found" not-found)

(router/redirect "/" "/home")
(router/redirect "/todo" "/todo/list")
(router/redirect "**" "/not-found")

(router/start)

;; === App Container ===

(defn app []
  [:div
   [:h2 "TODO App Header"]
   [:button {:on-click #(router/navigate "/")} "TODO Home"]
   [:button {:on-click #(router/navigate "/todo")} "TODO List"]
   [:button {:on-click #(router/navigate "/todo/1")} "TODO 1 Details"]
   [:button {:on-click #(router/navigate "/todo/2")} "TODO 2 Details"]
   [:button {:on-click #(router/navigate "/todo/create")} "TODO Create"]
   [:button {:on-click #(router/navigate "/todo/1/edit")} "TODO 1 Edit"]
   [:button {:on-click #(router/navigate "/todo/2/edit")} "TODO 2 Edit"]
   [:button {:on-click #(router/navigate "/some-not-defined-route")} "Not Found"]
   [:br]
   (router/outlet)
   [:small "TODO App Footer"]])

(defn mount-root []
  (r/render [app] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
