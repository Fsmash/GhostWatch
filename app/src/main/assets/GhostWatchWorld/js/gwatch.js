var World = {
	loaded: false,
	rotating: false,

	init: function initFn() {
		this.createModelAtLocation();
	},

	createModelAtLocation: function createModelAtLocationFn() {

		/*
			First a location where the model should be displayed will be defined. This location will be relative to the user.
		*/
		var location = new AR.RelativeLocation(null, 5, 0, 2);

		/*
			Next the model object is loaded.
		*/

        var indicatorImage = new AR.ImageResource("assets/indi.png");

        var indicatorDrawable = new AR.ImageDrawable(indicatorImage, 0.1, {
            verticalAnchor: AR.CONST.VERTICAL_ANCHOR.TOP
        });

        var imgLightning = new AR.ImageResource("assets/lightning.png");

        var lightning = new AR.AnimatedImageDrawable(imgLightning, 5, 128, 512);

        lightning.animate([0, 1, 2, 3, 4, 5, 6, 7], 100, -1);

        var modelGhost = new AR.Model("assets/boo.wt3", {
        	onLoaded: this.worldLoaded,
        	scale: {
        		x: 0.0005,
        		y: 0.0005,
        		z: 0.0005
        	}
        });

		/*
			Putting it all together the location and 3D model is added to an AR.GeoObject.
		*/
		var obj = new AR.GeoObject(location, {
            drawables: {
               cam: [lightning, modelGhost],
               indicator: [indicatorDrawable]
            }
        });
	},

	worldLoaded: function worldLoadedFn() {
		World.loaded = true;
		var e = document.getElementById('loadingMessage');
		e.parentElement.removeChild(e);
	}

};

World.init();
