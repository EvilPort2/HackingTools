function _foo([x, y], {z: z = 10}, ...args) {}

// ---
// Unused argument x (1:15)
// Unused argument y (1:18)
// Unused argument z (1:26)
// Unused argument args (1:38)
