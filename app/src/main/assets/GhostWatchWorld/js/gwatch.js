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

		var sound = new AR.Sound("assets/spooky.mp3");
        var boo = new AR.Sound("assets/boo.mp3");
        var blaster = new AR.Sound("assets/blaster.mp3");

		sound.play(-1);
		boo.play(-1);

        var indicatorImage = new AR.ImageResource("assets/indi.png");
        var indicatorDrawable = new AR.ImageDrawable(indicatorImage, 0.1, {
            verticalAnchor: AR.CONST.VERTICAL_ANCHOR.TOP
        });

        var explosionImage = new AR.ImageResource("assets/explosion.png");
        var explosion = new AR.AnimatedImageDrawable(explosionImage, 5, 192, 195, {
            onFinish: function() {this.opacity = 0.0;},
            opacity : 0.0
        });

        var imgLightning = new AR.ImageResource("assets/lightning.png");
        var lightning = new AR.AnimatedImageDrawable(imgLightning, 20, 128, 512, {
            onFinish: function() {this.opacity = 0.0;},
            opacity : 0.0,
            offsetY: -5.0
        });

        //var scaleLightning = new AR.PropertyAnimation(lightning, "scale", 0.25, 1, 500);
        var opacityLightning = new AR.PropertyAnimation(lightning, "opacity", 0.0, 1.0, 500);
        var opacityExplosion = new AR.PropertyAnimation(explosion, "opacity", 0.0, 1.0, 500);

        var modelGhost = new AR.Model("assets/boo.wt3", {
        	onLoaded: this.worldLoaded,
        	onClick: function() {
        	    blaster.play(1);
        	    //scaleLightning.start();
        	    opacityLightning.start();
        	    opacityExplosion.start();
        	    lightning.animate([0, 1, 2, 3, 4, 5, 6, 7], 100);
        	    explosion.animate([0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18,
        	    19, 20, 21, 22, 23, 24], 100);
        	},
        	scale: {
        		x: 0.0025,
        		y: 0.0025,
        		z: 0.0025
        	},
            translate: {
                x: 0,
                y: 0,
                z: -5
            }
        });

		/*
			Putting it all together the location and 3D model is added to an AR.GeoObject.
		*/
		var obj = new AR.GeoObject(location, {
		    onEnterFieldOfVision: function() {
                boo.stop();
		    },
		    onExitFieldOfVision: function() {
		        boo.play(-1);
		    },
            drawables: {
               cam: [explosion, lightning, modelGhost],
               indicator: [indicatorDrawable]
            }
        });
	},

	worldLoaded: function worldLoadedFn() {
		World.loaded = true;
		$("#loadingMessage").remove();
	}

};

World.init();
