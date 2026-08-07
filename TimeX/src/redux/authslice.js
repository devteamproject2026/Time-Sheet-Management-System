
  import { createSlice } from "@reduxjs/toolkit";

const authSlice = createSlice({
  name: "auth",
  initialState:{    
    user: null,  //this will contain id, username and role    
    isAuthenticated: false,
    // Protected routes must wait until /api/auth/me checks the JWT cookie.
    isInitializing: true,
  }, 
  reducers: {
    login: (state, action) => {
      state.user = action.payload.user;
      state.isAuthenticated = true;
      state.isInitializing = false;
    },

    restoreSession: (state, action) => {
      // The HttpOnly cookie contains the JWT, so only safe user details
      // need to be restored in Redux after a browser refresh.
      state.user = action.payload.user;
      state.isAuthenticated = true;
      state.isInitializing = false;
    },

    finishSessionCheck: (state) => {
      // No valid cookie was found. Routes may now redirect to the login page.
      state.isInitializing = false;
    },

    logout: (state) => {
      state.user = null;
      state.isAuthenticated = false;
      state.isInitializing = false;
    },
  },
});

export const {
  login,
  restoreSession,
  finishSessionCheck,
  logout,
} = authSlice.actions;
export default authSlice.reducer;
