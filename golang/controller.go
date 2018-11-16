package main

import (
	"encoding/json"
	"net/http"

	"github.com/gorilla/mux"

	"gopkg.in/mgo.v2"
	"gopkg.in/mgo.v2/bson"
)

// Controller object
type Controller struct {
	db   *mgo.Database
	coll *mgo.Collection
}

// NewController creates a posts controller
func NewController(db *mgo.Database) *Controller {
	return &Controller{
		db:   db,
		coll: db.C("posts"),
	}
}

// CreatePost REST resource
func (c *Controller) CreatePost(w http.ResponseWriter, r *http.Request) {
	var post Post
	if err := json.NewDecoder(r.Body).Decode(&post); err != nil {
		w.WriteHeader(400)
		return
	}
	post.ID = bson.NewObjectId()
	if err := c.coll.Insert(&post); err != nil {
		w.WriteHeader(500)
		return
	}
	json.NewEncoder(w).Encode(post)
}

// GetPost REST resource
func (c *Controller) GetPost(w http.ResponseWriter, r *http.Request) {
	id := mux.Vars(r)["id"]
	var post Post
	if err := c.coll.FindId(bson.ObjectIdHex(id)).One(&post); err != nil {
		w.WriteHeader(500)
		return
	}
	json.NewEncoder(w).Encode(post)
}
