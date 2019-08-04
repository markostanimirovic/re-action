(ns blog-example.posts.post-details)

(defn post-details-container [id]
  [:div
   [:h1 (str "Post details with id: " id)]])
