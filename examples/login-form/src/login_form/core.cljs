(ns login-form.core
  (:require [reagent.core :as r]
            [re-action.form :as form]
            [re-action.session :as session]))

(defn credentials-alert []
  (let [login-form (:state (session/get :login-form))]
    (fn []
      (when (not (nil? @login-form))
        [:div.alert.alert-success
         "Username: " (:username @login-form)
         ", Password: " (:password @login-form)]))))

(defn login-form []
  (let [login-form (form/create {:username {:required (comp not empty?)}
                                 :password {:required (comp not empty?)}})]
    (fn []
      [:form {:on-submit #(do (.preventDefault %)
                              (session/put! :login-form (form/value @login-form)))}
       [:div.form-group
        [:label {:for :username} "Username"]
        [:input.form-control {:id :username :type :text}]]
       [:div.form-group
        [:label {:for :password} "Password"]
        [:input.form-control {:id :password :type :password}]]
       [:button.btn.btn-primary {:disabled (not (form/valid? @login-form))} "Login"]])))

(defn app []
  [:div.container
   [:div.row.justify-content-center
    [:div.col-md-12 [credentials-alert]]
    [:div.col-md-12 [login-form]]]])

(defn mount-root []
  (r/render [app] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
