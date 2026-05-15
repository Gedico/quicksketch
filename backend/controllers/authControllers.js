const { registerUser, loginUser, checkAuthService } =
    require("../service/authService");

/**** REGISTER ******************************************************************/
const register = async (req, res) => {
    try {
        const result = await registerUser(req.body);

        res.json(result);

    } catch (err) {

        let message = "Errore registrazione";

        if (err.message === "EMAIL_EXISTS") {
            message = "Email già in uso";
        }

        if (err.message === "USERNAME_EXISTS") {
            message = "Username già in uso";
        }

        res.status(400).json({ message });
    }
};

/**** LOGIN *****************************************************************************/
const login = async (req, res) => {
    try {
        const { email, password } = req.body;

        const result = await loginUser(email, password);

        res.cookie("token", result.token, {
            httpOnly: true,
            sameSite: "Strict",
            secure: false,
            maxAge: 3600000
        });

        res.json({
            message: "Login effettuato",
            user: {
                id: result.user._id,
                username: result.user.username,
                email: result.user.email,
                punti: result.user.punti
            }
        });

    } catch (err) {
        res.status(400).json({ message: err.message });
    }
};


/*** CHECKAUTH *******************************************************************/
const checkAuth = (req, res) => {

    const token = req.cookies.token;

    const result = checkAuthService(token);

    if (!result.authenticated) {
        return res.status(401).json(result);
    }

    return res.status(200).json(result);
};



/**** LOGOUT (RESTA QUI) *******************************************************/
const logout = (req, res) => {
    res.clearCookie("token", {
        httpOnly: true,
        sameSite: "Strict",
        secure: false
    });

    res.json({ message: "Logout effettuato" });
};

module.exports = {
    register,
    login,
    logout,
    checkAuth
};