import * as mapboxgl from "mapbox-gl";
import * as p5 from "p5";

export declare class Mappa {
    constructor(typ: string, apiKey: string);

    tileMap(options: {}): MappaMap;
}

export declare class MappaMap {
    declare map: mapboxgl.Map;

    overlay(canvas: p5.Renderer): void;

    onChange(callback: () => void): void;

    latLngToPixel(latitude: Number, longitude: Number): any;

    zoom(): any;
}
