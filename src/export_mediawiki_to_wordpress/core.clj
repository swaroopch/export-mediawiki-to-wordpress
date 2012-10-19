(ns export-mediawiki-to-wordpress.core
  (:require [clojure.pprint :as pprint]
            [net.cgrand.enlive-html :as html]
            [clojure.string :as string]
            [export-mediawiki-to-wordpress.wordpress :as wp]))


(def ^:const mediawiki-base-url
  "http://www.swaroopch.org")


(def ^:const mediawiki-index-pages
  "http://www.swaroopch.org/notes/Special:AllPages"
  ["http://www.swaroopch.org/mediawiki/index.php?title=Special:AllPages&from=Acai_Berry_Forum_Supplement&to=Python_tr%3APreface"
   "http://www.swaroopch.org/mediawiki/index.php?title=Special:AllPages&from=Python_tr%3ATan%C4%B1t%C4%B1m&to=%E0%B4%86%E0%B4%AE%E0%B5%81%E0%B4%96%E0%B4%82"
   "http://www.swaroopch.org/mediawiki/index.php?title=Special%3AAllPages&from=&to=&namespace=2"])


(defn fetch-page
  [url]
  (html/html-resource (java.net.URL. url)))


(defn one-index-page-to-names
  [page]
  (map (comp #(assoc % :keep false) :attrs)
       (html/select (fetch-page page) [:table.mw-allpages-table-chunk :a])))


(defn index-pages-to-names
  "From the 'All Pages' MediaWiki Special Pages, get names of all pages.
http://www.swaroopch.org/notes/Special:AllPages"
  [pages]
  (mapcat one-index-page-to-names pages))


(defn enlive-to-html
  [nodes]
  (apply str (html/emit* nodes)))


(defn sanitize-html
  [content]
  (string/replace content " " ""))


(defn pick-content
  [content]
  (sanitize-html
   (enlive-to-html
    (html/at
     (html/select content [:div#bookname])
     [:h1.firstHeading] nil
     [:h3#siteSub] nil
     [:div#contentSub] nil
     [:div#jump-to-nav] nil
     [:div#catlinks] nil
     [:div.printfooter] nil
     [:p#bottom-notice] nil
     [html/comment-node] nil))))


(defn absolute-path
  [path]
  (assert (.startsWith path "/"))
  (str mediawiki-base-url path))


(defn post-to-wordpress
  [{:keys [title href]}]
  (let [path (last (string/split href #"/"))
        content (pick-content (fetch-page (absolute-path href)))
        new-post-id (wp/new-page title path content)
        new-post-path (:post_name (wp/get-page new-post-id))]
    (println "Saving" (str mediawiki-base-url href)
             "of size" (count content)
             "as" (str wp/wordpress-base-url new-post-path))
    new-post-id))


(defn dump-pages-list
  [filename]
  (spit filename
        (with-out-str
          (pprint/pprint
           (index-pages-to-names mediawiki-index-pages)))))


(defn process-pages-list
  [filename]
  (wp/verify-parent-page-exists)
  (let [pages (filter :keep
                      (read-string (slurp filename)))]
    (doseq [page pages]
      (post-to-wordpress page))))


(defn -main
  "Usage: lein run <dump OR process> <filename>"
  [step filename & args]
  (case step
    "dump" (dump-pages-list filename)
    "process" (process-pages-list filename)
    "<dump OR process> <filename>"))
