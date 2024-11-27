export interface LoginForm{
    email: string;
    code: string;
    password: string
}
export interface PicCode{
    pid:string;
    img:string;
    code:string;
}
const defaultLoginForm: LoginForm = {
    email: "",
    code: "",
    password:"",
}

const defaultPicCode: PicCode = {
    pid: "",
    code: "",
    img:"",
}

export const defaultValue = {
    defaultLoginForm,
    defaultPicCode
}
