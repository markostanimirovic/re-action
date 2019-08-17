(ns blog.posts.resource
  (:require [clojure.string :as string]))

(defonce ^:private posts (atom [{:id 1 :title "Post 1" :body "This is a post 1 body"}
                                {:id 2 :title "Post 2" :body (str "Lorem ipsum dolor sit amet, consectetur adipiscing elit,"
                                                                  " sed do eiusmod tempor incididunt ut labore et dolore magna"
                                                                  " aliqua. Ut enim ad minim veniam, quis nostrud exercitation"
                                                                  " ullamco laboris nisi ut aliquip ex ea commodo consequat."
                                                                  " Duis aute irure dolor in reprehenderit in voluptate velit"
                                                                  " esse cillum dolore eu fugiat nulla pariatur. Excepteur sint"
                                                                  " occaecat cupidatat non proident, sunt in culpa qui officia"
                                                                  " deserunt mollit anim id est laborum.")}
                                {:id 3 :title "Post 3" :body "This is a post 3 body"}
                                {:id 4 :title "Post 4" :body "This is a post 4 body"}
                                {:id 5 :title "Post 5" :body (str "Lorem ipsum dolor sit amet, consectetur adipiscing elit,"
                                                                  " sed do eiusmod tempor incididunt ut labore et dolore magna"
                                                                  " aliqua. Ut enim ad minim veniam, quis nostrud exercitation"
                                                                  " ullamco laboris nisi ut aliquip ex ea commodo consequat."
                                                                  " Duis aute irure dolor in reprehenderit in voluptate velit"
                                                                  " esse cillum dolore eu fugiat nulla pariatur. Excepteur sint"
                                                                  " occaecat cupidatat non proident, sunt in culpa qui officia"
                                                                  " deserunt mollit anim id est laborum.")}
                                {:id 6 :title "Post 6" :body "This is a post 6 body"}
                                {:id 7 :title "Post 7" :body "This is a post 7 body"}
                                {:id 8 :title "Post 8" :body "This is a post 8 body"}
                                {:id 9 :title "Post 9" :body "This is a post 9 body"}
                                {:id 10 :title "Post 10" :body (str "Lorem ipsum dolor sit amet, consectetur adipiscing elit,"
                                                                    " sed do eiusmod tempor incididunt ut labore et dolore magna"
                                                                    " aliqua. Ut enim ad minim veniam, quis nostrud exercitation"
                                                                    " ullamco laboris nisi ut aliquip ex ea commodo consequat."
                                                                    " Duis aute irure dolor in reprehenderit in voluptate velit"
                                                                    " esse cillum dolore eu fugiat nulla pariatur. Excepteur sint"
                                                                    " occaecat cupidatat non proident, sunt in culpa qui officia"
                                                                    " deserunt mollit anim id est laborum.")}
                                {:id 11 :title "Post 11" :body "This is a post 11 body"}
                                {:id 12 :title "Post 12" :body "This is a post 12 body"}
                                {:id 13 :title "Post 13" :body "This is a post 13 body"}
                                {:id 14 :title "Post 14" :body (str "Lorem ipsum dolor sit amet, consectetur adipiscing elit,"
                                                                    " sed do eiusmod tempor incididunt ut labore et dolore magna"
                                                                    " aliqua. Ut enim ad minim veniam, quis nostrud exercitation"
                                                                    " ullamco laboris nisi ut aliquip ex ea commodo consequat.")}
                                {:id 15 :title "Post 15" :body "This is a post 15 body"}
                                {:id 16 :title "Post 16" :body "This is a post 16 body"}]))

(defn get-posts [params]
  (->> @posts
       (filter (fn [post]
                 (some #(string/includes? (string/lower-case %) (string/lower-case (:search params)))
                       [(:title post) (:body post)])))
       (take (:selected-size params))))

(defn get-post [id]
  (->> @posts
       (filter #(= id (:id %)))
       (first)))

(defn update-post [post]
  (let [index (->> @posts
                   (keep-indexed #(if (= (:id %2) (:id post)) %1))
                   (first))]
    (swap! posts assoc index post)
    post))

(defn create-post [post]
  (let [id (->> @posts
                (map :id)
                (apply max)
                (inc))
        created-post (assoc post :id id)]
    (swap! posts conj created-post)
    created-post))
