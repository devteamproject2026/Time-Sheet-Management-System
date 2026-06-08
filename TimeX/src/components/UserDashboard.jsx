
import {NavLink ,Outlet} from "react-router-dom"
export default function UserDashboard(){

    return(
        <>
        
         <h2>User Panel</h2>
         <div className="d-flex">         

           <ul className="nav nav-pills flex-column p-3 border-end">
            <li className="nav-item">
            <NavLink to="users">Search</NavLink>
            </li>
            <li className="nav-item">
            <NavLink to="reports">Bookings</NavLink>
            </li>
            <li className="nav-item">   
            <NavLink to="/logout">Logout</NavLink>
            </li>
          </ul>
        
        <div className="p-3 flex-grow-1">
            <Outlet />
        </div>
        </div>

        
        </>
    )
}