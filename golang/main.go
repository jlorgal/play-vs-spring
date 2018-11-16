package main

import (
	"log"
	"net/http"

	"github.com/gorilla/mux"
	mgo "gopkg.in/mgo.v2"
)

func main() {
	session, err := mgo.Dial("localhost")
	if err != nil {
		panic(err)
	}
	defer session.Close()
	session.SetMode(mgo.Monotonic, true)
	database := session.DB("golang")
	controller := NewController(database)

	router := mux.NewRouter()
	router.HandleFunc("/v1/posts", controller.CreatePost).Methods("POST")
	router.HandleFunc("/v1/posts/{id}", controller.GetPost).Methods("GET")
	log.Fatal(http.ListenAndServe(":8080", router))
}
