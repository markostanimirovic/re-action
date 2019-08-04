(ns blog-example.posts.post-resource)

(defonce ^:private posts [{:id 1 :title "Post 1" :body "This is a post 1 body"}
                          {:id 2 :title "Post 2" :body "Lorem ipsum dolor sit amet, consectetur adipiscing elit,
                                                        sed do eiusmod tempor incididunt ut labore et dolore magna
                                                        aliqua. Ut enim ad minim veniam, quis nostrud exercitation
                                                        ullamco laboris nisi ut aliquip ex ea commodo consequat.
                                                        Duis aute irure dolor in reprehenderit in voluptate velit
                                                        esse cillum dolore eu fugiat nulla pariatur. Excepteur sint
                                                        occaecat cupidatat non proident, sunt in culpa qui officia
                                                        deserunt mollit anim id est laborum."}
                          {:id 3 :title "Post 3" :body "This is a post 3 body"}
                          {:id 4 :title "Post 4" :body "This is a post 4 body"}
                          {:id 5 :title "Post 5" :body "Lorem ipsum dolor sit amet, consectetur adipiscing elit,
                                                        sed do eiusmod tempor incididunt ut labore et dolore magna
                                                        aliqua. Ut enim ad minim veniam, quis nostrud exercitation
                                                        ullamco laboris nisi ut aliquip ex ea commodo consequat.
                                                        Duis aute irure dolor in reprehenderit in voluptate velit
                                                        esse cillum dolore eu fugiat nulla pariatur. Excepteur sint
                                                        occaecat cupidatat non proident, sunt in culpa qui officia
                                                        deserunt mollit anim id est laborum."}
                          {:id 6 :title "Post 6" :body "This is a post 6 body"}
                          {:id 7 :title "Post 7" :body "This is a post 7 body"}
                          {:id 8 :title "Post 8" :body "This is a post 8 body"}
                          {:id 9 :title "Post 9" :body "This is a post 9 body"}
                          {:id 10 :title "Post 10" :body "This is a post 10 body"}
                          {:id 11 :title "Post 11" :body "This is a post 11 body"}])

(defn get-posts [payload]
  posts
  (->> posts
       (filter #(or (re-find (js/RegExp. (:search payload) "i") (:title %))
                    (re-find (js/RegExp. (:search payload) "i") (:body %))))
       (take (:page-size payload))))
