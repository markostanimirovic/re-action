(ns todo-routing-app.prod
  (:require
    [todo-routing-app.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
