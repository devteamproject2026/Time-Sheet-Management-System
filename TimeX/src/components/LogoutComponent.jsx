import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { logout } from "../redux/authslice";
import { useEffect } from "react";
import { AUTH_API_URL } from "../config/api";

export default function LogoutComp() {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  useEffect(() => {

    fetch(`${AUTH_API_URL}/logout`, {
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
