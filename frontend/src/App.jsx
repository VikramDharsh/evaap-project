import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import Login from "./pages/Login";
import Signup from "./pages/Signup";

const App = () => (
  <BrowserRouter>
    <Routes>
      <Route path="/signup" element={<Signup />} />
      <Route path="/login" element={<Login />} />
      {/* Default redirect to signup */}
      <Route path="*" element={<Navigate to="/signup" replace />} />
    </Routes>
  </BrowserRouter>
);

export default App;
