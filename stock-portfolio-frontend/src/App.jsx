import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';

// Componenta care protejeaza rutele (Daca nu ai token, te arunca la Login)
const PrivateRoute = ({ children }) => {
    const token = localStorage.getItem('token');
    return token ? children : <Navigate to="/login" />;
};

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />

                {/* Ruta protejata: Dashboard */}
                <Route path="/dashboard" element={
                    <PrivateRoute>
                        <DashboardPage />
                    </PrivateRoute>
                } />

                {/* Orice altceva duce la dashboard (care duce la login daca n-ai token) */}
                <Route path="*" element={<Navigate to="/dashboard" />} />
            </Routes>
        </Router>
    );
}

export default App;