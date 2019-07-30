(ns blog-example.prod
  (:require
    [blog-example.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
