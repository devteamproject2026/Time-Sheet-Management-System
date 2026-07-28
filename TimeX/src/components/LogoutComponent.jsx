import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { logout } from "../redux/authslice";
import { useEffect } from "react";

export default function LogoutComp() {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  useEffect(() => {

    fetch("http://localhost:8081/api/auth/logout", {
      method: "POST",
      credentials: "include",
    })
      .finally(() => {
        dispatch(logout());
        navigate("/", { replace: true });
      });

  }, [dispatch, navigate]);

  return null;
}