# export-mediawiki-to-wordpress

A Clojure library to export from MediaWiki pages to Wordpress pages.

Why? Because I hate wiki spam. And I gave up trying to keep it out.

## Usage

1. Set environment variables expected in `wordpress.clj`
2. Change links in `core.clj` as per your Mediawiki installation
3. Install [Leiningen](http://leiningen.org)
4. Install library dependencies: `lein deps`
5. Run : `lein run dump pages.clj`
6. Edit `pages.clj` to change the `:keep` values, approving only
   those pages that you actually want to keep.
7. Run : `lein run process pages.clj`

## License

Copyright © 2012 [Swaroop C H](http://www.swaroopch.com).

Distributed under the Eclipse Public License, the same as Clojure.
