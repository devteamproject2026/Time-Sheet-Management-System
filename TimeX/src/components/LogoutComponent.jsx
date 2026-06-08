import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { logout } from "../redux/authslice";
import { useEffect } from "react";

export default function LogoutComp() {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  useEffect(() => {
    dispatch(logout());
    navigate("/", { replace: true });
  }, [dispatch, navigate]);

  return null;
}