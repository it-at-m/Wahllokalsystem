const passwordInput = document.getElementById("password");
const holdButton = document.getElementById("password-press-to-show");

if (passwordInput && holdButton) {
    const showPassword = () => {
        passwordInput.type = "text";
    };

    const hidePassword = () => {
        passwordInput.type = "password";
    };

    holdButton.addEventListener("pointerdown", showPassword);
    holdButton.addEventListener("pointerup", hidePassword);
    holdButton.addEventListener("pointerleave", hidePassword);
    holdButton.addEventListener("pointercancel", hidePassword);
    holdButton.addEventListener("keydown", (e) => {
        if (e.key === " ") showPassword();
    });
    holdButton.addEventListener("keyup", (e) => {
        if (e.key === " ") hidePassword();
    });
}
