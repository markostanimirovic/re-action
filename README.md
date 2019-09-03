# Re-Action

ClojureScript Framework for Building Single Page Applications

## Description

Re-Action is a ClojureScript framework for building reactive single page applications.
It uses [Reagent](https://github.com/reagent-project/reagent) to render components
and provides a lot of utility to developers such as state management, session, router and form helper.
Re-Action state management and data flow rely on Re-Streamer, library for reactive programming.
You can find more details about Re-Streamer [here](https://github.com/stanimirovic/re-streamer).

In Re-Action framework, code is organized by pages. Page has following parts:
- Facade
- Container Component
- Presentational Components

Facade contains state management for current page.
Container component delegates actions to the facade, facade responds to them and produces a new state
which is reflected in container. Container's template is divided into presentational components.

Re-Action architecture is shown in following image:

![Re-Action Architecture](https://github.com/stanimirovic/re-action/blob/master/resources/img/re-action-architecture.png)

Session is used in order to share the state between pages. Session is explained through example [here](#session).

## Usage

To use Re-Action in your Leiningen project, add following dependency in `project.clj`:

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
function from `re-action.core`. Streams that we need for our musicians container: `musicians`, `page-sizes`,
`selected-size` and `search`. Also we need to listen to the selected size and search criteria changes,
and for that purpose `get-musicians` stream is used. It is created using `select-distinct` function,
that will emit a new value only if it's not the same as an old value.
On every change into this stream, `get-musicians` function is called from `resource` namespace.

```clojure
(ns example.resource
  (:require [clojure.string :as string]))

(defn get-musicians [params]
  (->> ["Jimi Hendrix" "Eric Clapton" "Stevie Ray Vaughan" "Ritchie Blackmore"]
       (filter #(string/includes? % (:search params)))
       (take (:selected-size params))))
```

Resource should fetch musicians from the server, but in this simple example,
`get-musicians` function filters the vector of musicians by passed search criteria and selected size.

Return value of facade is a hash map of exposed data and functions to the container component.
Let's create musicians container and presentational components in `musicians` namespace.

```clojure
(defn- header [search update-search]
  [:div
   [:h2 "Posts"]
   [:input {:type        :text
            :placeholder "Search"
            :value       search
            :on-change   #(update-search (.. % -target -value))}]])

(defn- body [musicians]
  [:ul
   (for [musician musicians]
     [:li {:key (:id musician)} musician])])

(defn- footer [page-sizes selected-size update-selected-size]
  [:div
   (for [page-size page-sizes]
     [:button {:style    (when (= page-size selected-size) {:background-color :aqua})
               :key      page-size
               :on-click #(update-selected-size page-size)} page-size])])
```

There are three presentational components: `header`, `body` and `footer`. They don't contain any business
logic. So, data and functions are passed as input arguments.

```clojure
(defn container []
  (let [facade (facade)]
    (fn []
      [:div
       [header @(:search facade) (:update-search facade)]
       [body @(:musicians facade)]
       [footer @(:page-sizes facade) @(:selected-size facade) (:update-selected-size facade)]])))
```

Facade is initialized when the container is mounted. Container extracts data and functions from the facade,
and forwards them to the presentational components.

### Session

Session is a store for shared state between facades. Re-Action `session` namespace contains two functions:
`put!` and `get`. Now, let's walk through the simple example.

```clojure
(ns example.session
  (:require [re-action.session :as session]))

(defn foo-facade []
  {:update-foo-bar #(session/put! :foo-bar %)})

(defn bar-facade []
  (let [foo-bar (session/get :foo-bar)]
    {:foo-bar (:state foo-bar)}))
```

Let's suppose that foo page contains the input field for editing `foo-bar` property, and bar page contains
paragraph to display the value of `foo-bar`. Foo facade exposes `update-foo-bar` function to the
foo container. This function puts `foo-bar` value into session. On the other hand, bar facade gets `foo-bar`
property as a stream from the session and exposes it to the bar container.

### Router

Let's create three containers into `router.cljs`.

```clojure
(ns example.router
  (:require [re-action.router :as router]))

(defn home-container []
  [:h1 "Home Page"])

(defn posts-container []
  [:h1 "Posts Page"])

(defn post-details-container [id]
  [:div
   [:h1 "Post Details Page"]
   [:p (str "Id: " id)]])

(router/defroute "/home" home-container)
(router/defroute "/posts" posts-container)
(router/defroute "/posts/:id" post-details-container)
```

Function `defroute` is used to define the route for a particular page (container).
If route has parameters, they are passed to the container as input arguments.
Also, current route with params could be obtained from Re-Action session:

```clojure
(def current-route (session/get :current-route))
``` 

Re-Action `router` namespace also provides redirections:

```clojure
(router/redirect "/" "/home")
(router/redirect "**" "/home")
```

There is an option to define a not found redirection by using `**`.

Let's now define the application shell.

```clojure
(defn shell []
  [:div
   [:h1 "Header"]
   [:a {:on-click #(router/navigate "/")} "Home"]
   [:a {:on-click #(router/navigate "/posts")} "Posts"]
   [:a {:on-click #(router/navigate "/posts/1")} "Post 1"]
   [:a {:on-click #(router/navigate "/posts/2")} "Post 2"]
   [:a {:on-click #(router/navigate "/some-not-defined-route")} "Not Found"]
   [:br]
   (router/outlet)
   [:small "Footer"]])
```

Function `navigate` is used to change the current route.
Function `outlet` is a placeholder where current route's container will be rendered.

Lastly, it is necessary to call `start` function in order to start routing.

```clojure
(router/start)
```

### Form

Re-Action `form` namespace provides helper functions for managing forms.
Let's walk through these functions.

```clojure
(ns example.form
  (:require [re-action.form :as form]))

(defn create-person []
  (let [person-form (form/create {:first-name {:required   (comp not empty?)
                                               :max-length #(> 10 (count %))}
                                  :last-name  {:required   (comp not empty?)
                                               :min-length #(< 4 (count %))}})]
    (fn []
      [:form
       [:label "First Name"]
       [:input {:type :text :id :first-name}]
       [:div {:style {:color :red}}
        (when (form/touched? @person-form :first-name) [:p "First Name is touched."])
        (when (form/dirty? @person-form :first-name) [:p "First Name is dirty."])
        (when (not (form/valid? @person-form :first-name)) [:p "First Name is not valid."])]
       [:br]
       [:label "Last Name"]
       [:input {:type :text :id :last-name}]
       [:div {:style {:color :red}}
        (when (form/invalid-and-touched-or-dirty? @person-form :last-name)
          (list
            (when (not (form/valid? @person-form :last-name :required))
              [:p {:key :required} "Last Name is required field."])
            (when (not (form/valid? @person-form :last-name :min-length))
              [:p {:key :min-length} "Last Name must have at least 5 characters."])))]
       [:br]
       [:button {:type     :button
                 :on-click #(println (form/value @person-form))
                 :disabled (not (form/valid? @person-form))} "Save"]])))
```

First function of this namespace is `create`. It accepts configuration in format:

```clojure
{:field1-id {:validator11-key :validator11
             :validator12-key :validator12}
 :field2-id {:validator21-key :validator21
             :validator22-key :validator22
             :validator23-key :validator23}}
```

Function `value` returns a value of passed form in format:

```clojure
{:field1-id :field1-value
 :field2-id :field2-value}
```

If `touched?`/`dirty?`/`valid?` function is called with form, it will return `true` if form is touched/dirty/valid.
If some of these three functions are called with form and field's id, it will return `true` if passed field from that
form is touched, dirty or valid. Also, `valid?` can accept validator key as a third argument and in that case,
it will return `true` if field is valid by passed validator.

However, there are derived functions from this namespace:
`touched-or-dirty?`, `valid-and-touched-or-dirty?`, `invalid-and-touched-or-dirty?`.

For more details, check [examples](https://github.com/stanimirovic/re-action/tree/master/examples) directory.

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
