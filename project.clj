(defproject hh-website "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [stasis "2.5.1"]
                 [ring "1.9.5"]]
  :plugins [[lein-ring "0.12.6"]]
  :main ^:skip-aot hh-website.core
  :target-path "target/%s"
  :profiles {:ring    {:handler core/app}
             :uberjar {:aot      :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
