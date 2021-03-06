var World = {
	loaded: false,

	init: function initFn() {
		this.createModelAtLocation();
	},

	createModelAtLocation: function createModelAtLocationFn() {

		/*
			First a location where the model should be displayed will be defined. This location will be relative to the user.
		*/
		var location = new AR.RelativeLocation(null, 5, 0, 2);

		var sound = new AR.Sound("assets/spooky.mp3");
		var ambient = new AR.Sound("assets/ambient.mp3");
        var boo = new AR.Sound("assets/boo.mp3");
        var blaster = new AR.Sound("assets/blaster.mp3");
        var laugh = new AR.Sound("assets/laugh.mp3");

		sound.play(-1);
		ambient.play(-1);
		boo.play(-1);

        var indicatorImage = new AR.ImageResource("assets/indi.png");
        var indicatorDrawable = new AR.ImageDrawable(indicatorImage, 0.1, {
            verticalAnchor: AR.CONST.VERTICAL_ANCHOR.TOP
        });

        var explosionImage = new AR.ImageResource("assets/explosion.png");
        var explosion = new AR.AnimatedImageDrawable(explosionImage, 5, 192, 195, {
            onFinish: function() {this.opacity = 0.0;},
            zOrder: 2,
            opacity : 0.0
        });

        var imgLightning = new AR.ImageResource("assets/lightning.png");
        var lightning = new AR.AnimatedImageDrawable(imgLightning, 20, 128, 512, {
            onFinish: function() {this.opacity = 0.0;},
            zOrder: 1,
            opacity : 0.0,
            offsetY: -5.0
        });

        //var scaleLightning = new AR.PropertyAnimation(lightning, "scale", 0.25, 1, 500);
        var opacityLightning = new AR.PropertyAnimation(lightning, "opacity", 0.0, 1.0, 500);
        var opacityExplosion = new AR.PropertyAnimation(explosion, "opacity", 0.0, 1.0, 500);

        var modelGhost = new AR.Model("assets/boo.wt3", {
        	onLoaded: this.worldLoaded,
        	onClick: function() {
        	    if ($("#ray").is(':visible')) {
                    blaster.play(1);
                    //scaleLightning.start();
                    opacityLightning.start();
                    opacityExplosion.start();
                    lightning.animate([0, 1, 2, 3, 4, 5, 6, 7], 100);
                    explosion.animate([0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18,
                    19, 20, 21, 22, 23, 24], 100);
                }
        	},
        	scale: {
        		x: 0.0025,
        		y: 0.0025,
        		z: 0.0025
        	},
            translate: {
                x: 0,
                y: 0,
                z: -200
            }
        });

        var rotateGhostY = new AR.PropertyAnimation(modelGhost, "rotate.heading", 0, 360, 1000);
        var rotateGhostZ = new AR.PropertyAnimation(modelGhost, "rotate.roll", 0, 360, 2500);
        var appearGhost = new AR.PropertyAnimation(modelGhost, "translate.z", -200, -5, 2500);
        var rightGhost = new AR.PropertyAnimation(modelGhost, "translate.x", modelGhost.translate.x, 5, 2000, {
            onFinish : function() {
            modelGhost.translate.x = 5;
            lightning.offSetX = 5;
            explosion.offsetX = 5;
            }
        });
        var leftGhost = new AR.PropertyAnimation(modelGhost, "translate.x", modelGhost.translate.x, -5, 2000, {
            onFinish : function() {
            modelGhost.translate.x = -5;
            lightning.offSetX = -5;
            explosion.offsetX = -5;
            }
        });

		/*
			Putting it all together the location and 3D model is added to an AR.GeoObject.
		*/
		var appear = false;

		setInterval(function() {
		    rotateGhostY.start();
		    laugh.play();
		}, 15000);
		/*
		setInterval(function() {
		    var rand = Math.floor((Math.random() * 2));
		    if (rand > 0) {
		        rightGhost.start();
		    } else {
		        leftGhost.start();
		    }
		}, 45000); */

        var animations = new AR.GeoObject(location, {
            drawables: {
                cam: [explosion, lightning],
            },
            renderingOrder: 1
        });

		var obj = new AR.GeoObject(location, {
		    onEnterFieldOfVision: function() {
                boo.stop();
                if (!appear) {
                    rotateGhostZ.start();
                    appearGhost.start();
                    appear = true;
                }
		    },
		    onExitFieldOfVision: function() {
		        boo.play(-1);
		    },
            drawables: {
               cam: [modelGhost],
               indicator: [indicatorDrawable]
            },
            renderingOrder: 0
        });
	},

	worldLoaded: function worldLoadedFn() {
		World.loaded = true;
		$("#loadingMessage").remove();
	}

};

World.init();
