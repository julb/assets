const collections = [
	"agreements",
	"announcements",
	"bookmarks",
	"component-categories",
	"components",
	"disclaimers",
	"incidents",
	"incidents-components",
	"incidents-history",
    "links",
	"planned-maintenances",
	"planned-maintenances-history",
	"planned-maintenances-components",
	"users",
	"users-authentications",
	"users-mails",
	"users-mobile-phones",
	"users-preferences",
	"users-profile",
	"users-sessions"
];

collections.forEach((collection) => {
    print("> Dropping/Creating collection: " + collection);
	db.getCollection(collection).drop();
	db.createCollection(collection);
});
