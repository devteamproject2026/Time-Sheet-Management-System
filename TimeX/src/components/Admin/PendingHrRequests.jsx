import { useEffect, useState } from "react";

export default function PendingHrRequests() {
  const [hrs, setHrs] = useState([]);

  useEffect(() => {
    fetch("http://localhost:9000/pending-hr")
      .then((resp) => resp.json())
      .then((data) => {
        setHrs(data);
      });
  }, []);

  const approveHr = (id) => {
    fetch(`http://localhost:9000/approve-hr/${id}`, {
      method: "PUT",
    }).then(() => {
      setHrs(hrs.filter((hr) => hr.userid !== id));
    });
  };

  const rejectHr = (id) => {
    fetch(`http://localhost:9000/reject-hr/${id}`, {
      method: "PUT",
    }).then(() => {
      setHrs(hrs.filter((hr) => hr.userid !== id));
    });
  };

  return (
    <>
      <h2>Pending HR Requests</h2>

      <table border="1">
        <thead>
          <tr>
            <th>ID</th>
            <th>Username</th>
            <th>Email</th>
            <th>Approve</th>
            <th>Reject</th>
          </tr>
        </thead>

        <tbody>
          {hrs.map((hr) => (
            <tr key={hr.userid}>
              <td>{hr.userid}</td>
              <td>{hr.username}</td>
              <td>{hr.email}</td>

              <td>
                <button onClick={() => approveHr(hr.userid)}>Approve</button>
              </td>

              <td>
                <button onClick={() => rejectHr(hr.userid)}>Reject</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </>
  );
}
