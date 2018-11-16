package main

import "gopkg.in/mgo.v2/bson"

// Post model
type Post struct {
	ID    bson.ObjectId `bson:"_id" json:"id"`
	Title string        `bson:"title" json:"title"`
	Body  string        `bson:"body" json:"body"`
}
