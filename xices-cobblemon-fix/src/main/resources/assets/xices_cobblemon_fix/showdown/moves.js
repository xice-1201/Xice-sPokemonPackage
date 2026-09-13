"use strict";
const Moves = {
  wardenpunch: { num: -10002, name: "Warden Punch", type: "Normal", category: "Physical", basePower: 140, accuracy: 100, pp: 20, priority: 0, target: "normal", flags: { contact: 1 } },
  sonicwave: { num: -10003, name: "Sonic Wave", type: "Dark", category: "Special", basePower: 120, accuracy: true, pp: 15, priority: 0, target: "normal", flags: { sound: 1 }, breaksProtect: true },
  darksurge: { num: -10004, name: "Dark Surge", type: "Normal", category: "Status", accuracy: true, pp: 5, priority: 0, target: "allAdjacent", boosts: { accuracy: -1 } }
  ,soniclock: { num: -10005, name: "Sonic Lock", type: "Dark", category: "Status", accuracy: 100, pp: 20, priority: 0, target: "normal" }
};
module.exports = { Moves };
