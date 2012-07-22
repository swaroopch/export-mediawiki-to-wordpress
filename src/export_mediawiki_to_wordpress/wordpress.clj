(ns export-mediawiki-to-wordpress.wordpress
  (:require [timbre.core :as log]
            [necessary-evil.core :as xmlrpc]))


(def wordpress-rpc-url
  "https://codex.wordpress.org/XML-RPC_Support"
  (System/getenv "WORDPRESS_RPC_URL"))


(def wordpress-blog-id
  "https://codex.wordpress.org/XML-RPC_WordPress_API/Users#wp.getUsersBlogs"
  (System/getenv "WORDPRESS_BLOG_ID"))


(def wordpress-username
  (System/getenv "WORDPRESS_USERNAME"))


(def wordpress-password
  (System/getenv "WORDPRESS_PASSWORD"))


(def parent-page-id
  "All new pages will go under this, for example a '/notes' page"
  (System/getenv "WORDPRESS_PARENT_PAGE_ID"))


(def parent-page-slug
  "Example: 'notes', so new pages will be /notes/new_page"
  (System/getenv "WORDPRESS_PARENT_PAGE_SLUG"))


(defn new-page
  "https://codex.wordpress.org/XML-RPC_WordPress_API/Posts#wp.newPost"
  [title slug body]
  (xmlrpc/call
   wordpress-rpc-url
   "wp.newPost"
   wordpress-blog-id
   wordpress-username
   wordpress-password
   {:post_type "page"
    :post_status "publish"
    :post_parent parent-page-id
    :post_title title
    :post_name slug
    :post_content body}))


(defn get-page
  "https://codex.wordpress.org/XML-RPC_WordPress_API/Posts#wp.getPost"
  [post-id]
  (xmlrpc/call
   wordpress-rpc-url
   "wp.getPost"
   wordpress-blog-id
   wordpress-username
   wordpress-password
   post-id))
