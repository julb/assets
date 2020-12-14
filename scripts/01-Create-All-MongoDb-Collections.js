const collections = [
	"announcements",
	"component-categories",
	"components",
	"disclaimers",
	"incidents",
	"incidents-components",
	"incidents-history",
        "links",
	"planned-maintenances",
	"planned-maintenances-history",
	"planned-maintenances-components"
];

collections.forEach((collection) => {
        print("> Dropping/Creating collection: " + collection);
	db.getCollection(collection).drop();
	db.createCollection(collection);
});
