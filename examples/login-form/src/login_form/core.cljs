(ns login-form.core
  (:require [reagent.core :as r]
            [re-action.form :as form]
            [re-action.session :as session]))

(defn credentials-alert []
  (let [login-form (:state (session/get :login-form))]
    (fn []
      (when (not (nil? @login-form))
        [:div.alert.alert-success
         [:b "Login Credentials:"]
         " Username: " (:username @login-form)
         ", Password: " (:password @login-form)]))))

(defn login-form []
  (let [login-form (form/create {:username {:required (comp not empty?)}
                                 :password {:required   (comp not empty?)
                                            :min-length #(< 4 (count %))
                                            :strength   #(.test (js/RegExp "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])") %)}})]
    (fn []
      [:form {:on-submit     #(do (.preventDefault %)
                                  (session/put! :login-form (form/value @login-form)))
              :auto-complete :off}
       [:div.form-group
        [:label {:for :username} "Username" [:span.text-danger " *"]]
        [:input.form-control {:id    :username
                              :type  :text
                              :class (list
                                       (when (form/valid-and-touched-or-dirty? @login-form :username) :is-valid)
                                       (when (form/invalid-and-touched-or-dirty? @login-form :username) :is-invalid))}]
        (when (form/invalid-and-touched-or-dirty? @login-form :username)
          [:div.invalid-feedback "Username is required field."])]
       [:div.form-group
        [:label {:for :password} "Password" [:span.text-danger " *"]]
        [:input.form-control {:id    :password
                              :type  :password
                              :class (list
                                       (when (form/valid-and-touched-or-dirty? @login-form :password) :is-valid)
                                       (when (form/invalid-and-touched-or-dirty? @login-form :password) :is-invalid))}]
        (when (form/invalid-and-touched-or-dirty? @login-form :password)
          (list
            (when (not (form/valid? @login-form :password :required))
              [:div.invalid-feedback {:key :required} "Password is required field."])
            (when (not (form/valid? @login-form :password :min-length))
              [:div.invalid-feedback {:key :min-length} "Password must have at least 5 characters."])
            (when (not (form/valid? @login-form :password :strength))
              [:div.invalid-feedback {:key :strength}
               "Password must contain upper case letters, lower case letters and numbers."])))]
       [:button.btn.btn-primary {:disabled (not (form/valid? @login-form))} "Login"]])))

(defn app []
  [:div.container.buffer-top
   [:div.row.justify-content-center
    [:div.col-md-9 [credentials-alert]]]
   [:div.row.justify-content-center
    [:div.col-md-9 [login-form]]]])

(defn mount-root []
  (r/render [app] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
