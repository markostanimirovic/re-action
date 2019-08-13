(ns login-form.prod
  (:require
    [login-form.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
