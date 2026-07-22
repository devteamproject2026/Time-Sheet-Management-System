// import { StrictMode } from 'react'
// import { createRoot } from 'react-dom/client'
// import './index.css'
// // import 'bootstrap/dist/css/bootstrap.min.css';
// import "../node_modules/bootstrap/dist/css/bootstrap.min.css"
// import App from './App.jsx'
// import { store } from "./redux/store.js";
// import { Provider } from "react-redux";

// createRoot(document.getElementById('root')).render(
//   <Provider store={store}>
//   <StrictMode>
//     <App />
//   </StrictMode>
//   </Provider>,
// )


import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import 'bootstrap/dist/css/bootstrap.min.css'
import App from './App.jsx'
import { store } from "./redux/store.js"
import { Provider } from "react-redux"

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <Provider store={store}>
      <App />
    </Provider>
  </StrictMode>
)