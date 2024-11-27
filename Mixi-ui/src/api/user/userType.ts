export type Profile = {
    username: string;
    email: string;
    password: string;
    avatar: string;
    nickname: string;
    sex: string;
    resume: string;
}

export type LinkLoginForm = {
    email:string;
    picId:string;
    picCode:string;
}
export type LinkVerifyForm = {
    linkToken:string;
}
export type VisitorLoginForm = {
    fingerprint:string;
    // picId:string;
    // picCode:string;
}
