# Re-Action

ClojureScript Framework for Building Single Page Applications

## Description

Re-Action is a ClojureScript framework for building reactive single page applications.
It uses Reagent to render components and gives many utilities to developers
such as state management, session, routing and forms managing.

In Re-Action framework, code is organized by pages. Page has following parts:
- Facade
- Container component
- Presentational components

Facade contains state management of current page and its presentational logic.
Container component delegates actions to the facade, facade responds to them and produces a new state
which is reflected in container. Container's template is divided into presentational components.

Re-Action architecture is shown in following image:

![Re-Action Architecture](https://github.com/stanimirovic/re-action/blob/master/resources/img/re-action-architecture.png)

Session is used in order to share the state between pages. Session is explained through example [here](#session).

## Usage

To use Re-Action in your Leiningen project, add this dependency in `project.clj`:

[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.stanimirovic/re-action.svg)](https://clojars.org/org.clojars.stanimirovic/re-action)

## Examples

As already stated, page's state is managed in facade. So, let's create `musicians.cljs` and facade into it.

```clojure
(ns example.musicians
  (:require [example.resource :as resource]
            [re-action.core :as re-action]
            [re-streamer.core :refer [subscribe]]))

(defn- facade []
  (let [init-state {:musicians [] :page-sizes [1 3 5] :selected-size 1 :search ""}
        store (re-action/store init-state)
        musicians (re-action/select store :musicians)
        page-sizes (re-action/select store :page-sizes)
        selected-size (re-action/select store :selected-size)
        search (re-action/select store :search)
        get-musicians (re-action/select-distinct store :selected-size :search)]

    (subscribe get-musicians #(re-action/patch-state! store {:musicians (resource/get-musicians %)}))

    {:musicians            (:state musicians)
     :page-sizes           (:state page-sizes)
     :selected-size        (:state selected-size)
     :search               (:state search)
     :update-selected-size #(re-action/patch-state! store {:selected-size %})
     :update-search        #(re-action/patch-state! store {:search %})}))
```

First, we need to define initial state of the page. It contains musicians vector, possible page sizes,
selected size and search criteria. After that, we define musicians page store. Store is created using
`store` function from `re-action.core` namespace.

Next step is to decompose the state from store to separate streams of data. This is made possible using `select`
function from `re-action.core`. Streams that we need for our musicians page: `musicians`, `page-sizes`,
`selected-size` and `search`. Also we need to listen to the selected size and search criteria changes,
and for that purpose `get-musicians` stream is created. On every change into this stream,
`get-musicians` function is called from `resource` namespace.

```clojure
(ns example.resource
  (:require [clojure.string :as string]))

(defn get-musicians [params]
  (->> ["Jimi Hendrix" "Eric Clapton" "Steve Ray Vaughan" "Ritchie Blackmore"]
       (filter #(string/includes? % (:search params)))
       (take (:selected-size params))))
```

Resource should fetch musicians from the server, but in this simple example,
`get-musicians` function filters the vector of musicians by passed search criteria and selected size.

Return value of facade is a hash map of exposed data and functions to the container component.
Let's create container and presentational components for musicians page in `musicians` namespace.

```clojure
(defn- header [search update-search]
  [:div.card-header
   [:h5 "Posts"]
   [:input.form-control {:type        :text
                         :placeholder "Search"
                         :value       search
                         :on-change   #(update-search (.. % -target -value))}]])

(defn- body [musicians]
  [:div.card-body.row
   (for [musician musicians]
     [:div.col-md-3.col-sm-4.mb-3 {:key (:id musician)}
      [:div.card
       [:div.card-body musician]]])])

(defn- footer [page-sizes selected-size update-selected-size]
  [:div.card-footer.text-center
   (for [page-size page-sizes]
     [:button.btn.mr-1 {:class    (if (= page-size selected-size) "btn-primary" "btn-light")
                        :key      page-size
                        :on-click #(update-selected-size page-size)} page-size])])
```

There are three presentational components: `header`, `body` and `footer`. They don't contain any business
logic. So, data and actions are passed as input parameters. For styling is used Bootstrap 4.

```clojure
(defn container []
  (let [facade (facade)]
    (fn []
      [:div.card
       [header @(:search facade) (:update-search facade)]
       [body @(:musicians facade)]
       [footer @(:page-sizes facade) @(:selected-size facade) (:update-selected-size facade)]])))
```

Facade is initialized when the container is mounted. Container extracts data and actions from the facade,
and passes them to presentational components.

### Session

Session is a store for shared state between facades. Re-Action's `session` namespace contains two functions
`put!` and `get`. Now, let's walk through simple example.

```clojure
(ns example.session
  (:require [re-action.session :as session]))

(defn foo-facade []
  {:update-foo-bar #(session/put! :foo-bar %)})

(defn bar-facade []
  (let [foo-bar (session/get :foo-bar)]
    {:foo-bar (:state foo-bar)}))
```

Let's suppose that foo page contains the input field for editing foo-bar property, and bar page contains
paragraph for displaying a value of foo-bar property. Foo facade exposes `update-foo-bar` function to the
foo container. This function puts foo bar value to the session. On the other hand, bar facade gets foo-bar
property as a stream from the session and exposes it to the bar container.

### Routing

### Forms


## License

Copyright © 2019 Marko Stanimirovic

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
