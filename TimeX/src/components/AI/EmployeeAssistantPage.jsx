import { useState } from "react";
import { aiApi } from "../../services/apiClient";
import "./assistant.css";

const starterMessages = [
  "What tasks are currently pending?",
  "Show my completed tasks.",
  "Which task should I work on now?",
  "Tell me a short workplace joke.",
];

export default function EmployeeAssistantPage() {
  const [messages, setMessages] = useState([
    {
      role: "assistant",
      text: "Hello! Ask me about your tasks, or start a friendly conversation.",
    },
  ]);
  const [message, setMessage] = useState("");
  const [sending, setSending] = useState(false);

  const sendMessage = async (text) => {
    const question = text.trim();
    if (!question || sending) return;

    setMessages((current) => [...current, { role: "user", text: question }]);
    setMessage("");
    setSending(true);

    try {
      const response = await aiApi("/chat", {
        method: "POST",
        body: { message: question },
      });
      setMessages((current) => [
        ...current,
        { role: "assistant", text: response.answer },
      ]);
    } catch (error) {
      setMessages((current) => [
        ...current,
        { role: "error", text: error.message },
      ]);
    } finally {
      setSending(false);
    }
  };

  const submit = (event) => {
    event.preventDefault();
    sendMessage(message);
  };

  return (
    <section className="assistant-page">
      <header className="assistant-header">
        <div>
          <p className="business-kicker">Employee Support</p>
          <h1>WorkPlus AI Assistant</h1>
          <p>Ask about your assigned tasks or have a short, friendly conversation.</p>
        </div>
        <span className="assistant-status"><i className="bi bi-shield-check" /> Secure task context</span>
      </header>

      <div className="assistant-layout">
        <aside className="assistant-suggestions">
          <h2>Try asking</h2>
          {starterMessages.map((starter) => (
            <button key={starter} type="button" onClick={() => sendMessage(starter)}>
              {starter}
            </button>
          ))}
          <p>Only your own task records are provided to the assistant.</p>
        </aside>

        <div className="assistant-chat">
          <div className="assistant-messages" aria-live="polite">
            {messages.map((item, index) => (
              <div key={`${item.role}-${index}`} className={`assistant-message assistant-message--${item.role}`}>
                <span>{item.role === "user" ? "You" : item.role === "error" ? "Error" : "Assistant"}</span>
                <p>{item.text}</p>
              </div>
            ))}
            {sending && (
              <div className="assistant-message assistant-message--assistant">
                <span>Assistant</span>
                <p>Checking your tasks...</p>
              </div>
            )}
          </div>

          <form className="assistant-form" onSubmit={submit}>
            <label htmlFor="assistantMessage" className="visually-hidden">Ask about your tasks</label>
            <textarea
              id="assistantMessage"
              value={message}
              onChange={(event) => setMessage(event.target.value)}
              placeholder="Example: What tasks are in progress?"
              maxLength="500"
              rows="2"
              disabled={sending}
            />
            <button type="submit" disabled={sending || !message.trim()}>
              <i className="bi bi-send" /> {sending ? "Sending" : "Send"}
            </button>
          </form>
        </div>
      </div>
    </section>
  );
}
