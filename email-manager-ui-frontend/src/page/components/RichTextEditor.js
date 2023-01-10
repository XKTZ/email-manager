import ReactQuill from "react-quill";
import "react-quill/dist/quill.snow.css"

const modules = {
    toolbar: [
        [{'header': [1, 2, false]}],
        ['bold', 'italic', 'underline', 'strike', 'code-block', 'blockquote'],
        [{'list': 'ordered'}, {'list': 'bullet'}, {'indent': '-1'}, {'indent': '+1'}],
        ['link', 'image'],
        ['clean']
    ]
};



export default ({content, setContent, style, ...props}) => {
    return (
        <ReactQuill theme={"snow"}
                    modules={modules}
                    value={content}
                    onChange={setContent}
                    style={style}
                    {...props}
                    />
    )
}