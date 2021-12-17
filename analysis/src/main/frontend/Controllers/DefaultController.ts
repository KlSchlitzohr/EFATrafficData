// API Key for MapboxGL. Get one here:
// https://www.mapbox.com/studio/account/tokens/
//@ts-ignore
//import {MappaMap} from "../typings/MappaMundi";

const apiKey = '';

// Create an instance of MapboxGL
// @ts-ignore
const mappa = new Mappa('MapboxGL', apiKey);

// Options for map
const options = {
    lat: 0,
    lng: 0,
    zoom: 4,
    studio: true, // false to use non studio styles
    //style: 'mapbox.dark' //streets, outdoors, light, dark, satellite (for nonstudio)
    style: 'mapbox://styles/mapbox/traffic-night-v2',
};

let canvas;
let myMap: any;
let meteorites: any;

function preload() {
    // Load the data
    // @ts-ignore
    meteorites = loadTable('data/Meteorite_Landings.csv', 'csv', 'header');
}

function setup() {
    canvas = createCanvas(640, 500);
    console.log(canvas);

    // Create a tile map and overlay the canvas on top.
    myMap = mappa.tileMap(options);
    myMap.overlay(canvas);

    myMap.onChange(drawMeteorites);

    fill(109, 255, 0);
    stroke(100);
}

// The draw loop is fully functional but we are not using it for now.
function draw() {}

function drawMeteorites() {
    // Clear the canvas
    clear();

    for (let i = 0; i < meteorites.getRowCount(); i += 1) {
        // Get the lat/lng of each meteorite
        const latitude = Number(meteorites.getString(i, 'reclat'));
        const longitude = Number(meteorites.getString(i, 'reclong'));

        // Only draw them if the position is inside the current map bounds. We use a
        // Mapbox method to check if the lat and lng are contain inside the current
        // map. This way we draw just what we are going to see and not everything. See
        // getBounds() in https://www.mapbox.com/mapbox.js/api/v3.1.1/l-latlngbounds/
        /* if (myMap.map.getBounds().co, latitude])) {*/
            // Transform lat/lng to pixel position
            const pos = myMap.latLngToPixel(latitude, longitude);
            // Get the size of the meteorite and map it. 60000000 is the mass of the largest
            // meteorite (https://en.wikipedia.org/wiki/Hoba_meteorite)
            let size = meteorites.getString(i, 'mass (g)');
            size = map(size, 558, 60000000, 1, 25) + myMap.zoom();
            ellipse(pos.x, pos.y, size, size);
        /*}*/
    }
}
