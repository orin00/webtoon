const express = require('express');
const path = require('path');
const fs = require('fs');
const nodemailer = require('nodemailer');
const cors = require('cors');
const fileUpload = require('express-fileupload');

const app = express();
const port = 5000;

app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(fileUpload()); // express-fileupload 미들웨어 추가


console.log('현재 디렉토리:', __dirname);

require('dotenv').config();

// Nodemailer 설정
const transporter = nodemailer.createTransport({
    service: 'Naver', // 사용하고 있는 이메일 서비스
    host: 'smtp.naver.com',
    port: 465,
    auth: {
        user: process.env.NAVER_EMAIL, // 관리자의 이메일
        pass: process.env.NAVER_PASS // 관리자의 이메일 비밀번호
    }
});

// 이미지 업로드 API
app.post('/api/upload', (req, res) => {
    const aId = req.body.aId; // 선택한 작품의 aId
    const episodeCount = req.body.episodeCount; // 에피소드 카운트
    const baseDir = path.join(__dirname, '..', 'client', 'public', 'assets', 'webtoonImage', `artwork_id${aId}`, `episode_count${episodeCount}`);

    console.log('기본 디렉토리:', baseDir);

    // 폴더가 존재하지 않으면 생성
    fs.mkdir(baseDir, { recursive: true }, (err) => {
        if (err) {
            console.error('폴더 생성 중 오류 발생:', err);
            return res.status(500).send('폴더 생성 중 오류 발생');
        }
        console.log('기본 폴더가 성공적으로 생성되었습니다:', baseDir);

        // episode_url, ppl_ad_url, thumbnail_url 폴더 생성
        const subDirs = ['episode_url', 'ppl_ad_url', 'thumbnail_url'];
        subDirs.forEach(dir => {
            const subDirPath = path.join(baseDir, dir);
            fs.mkdirSync(subDirPath, { recursive: true });
            console.log(`${dir} 폴더가 성공적으로 생성되었습니다:`, subDirPath);
        });

        // 업로드할 파일을 처리
        const files = req.files; // 모든 업로드된 파일 가져오기
        if (!files) {
            return res.status(400).send('파일이 제공되지 않았습니다.');
        }

        // 각각의 파일을 지정된 위치에 저장
        const thumbnailFile = files.thumbnail;
        const pplFile = files.ppl;
        const episodesFiles = Array.isArray(files.episodes) ? files.episodes : [files.episodes];

        if (thumbnailFile) {
            const thumbnailPath = path.join(baseDir, 'thumbnail_url', thumbnailFile.name);
            thumbnailFile.mv(thumbnailPath, (err) => {
                if (err) {
                    return res.status(500).send('썸네일 파일 저장 중 오류 발생');
                }
                console.log('썸네일 파일이 성공적으로 저장되었습니다:', thumbnailPath);
            });
        }

        if (pplFile) {
            const pplPath = path.join(baseDir, 'ppl_ad_url', pplFile.name);
            pplFile.mv(pplPath, (err) => {
                if (err) {
                    return res.status(500).send('PPL 파일 저장 중 오류 발생');
                }
                console.log('PPL 파일이 성공적으로 저장되었습니다:', pplPath);
            });
        }

        // 에피소드 파일 저장
        episodesFiles.forEach((episodeFile) => {
            const episodePath = path.join(baseDir, 'episode_url', episodeFile.name);
            episodeFile.mv(episodePath, (err) => {
                if (err) {
                    return res.status(500).send('에피소드 파일 저장 중 오류 발생');
                }
                console.log('에피소드 파일이 성공적으로 저장되었습니다:', episodePath);
            });
        });

        res.json({ message: '이미지 업로드 성공' });
    });
});

app.post('/api/artworks/submitProfile', (req, res) => {
    const { artworkId } = req.body; // artworkId를 요청 본문에서 가져옴
    const profileImage = req.files?.profile;

    if (!artworkId) {
        return res.status(400).send('artworkId가 제공되지 않았습니다.');
    }

    if (!profileImage) {
        return res.status(400).send('프로필 이미지 파일이 제공되지 않았습니다.');
    }

    const dirPath = path.join(__dirname, '..', 'client', 'public', 'assets', 'webtoonImage', `artwork_id${artworkId}`, 'profile_image');

    // 디렉토리가 존재하지 않으면 생성
    fs.mkdir(dirPath, { recursive: true }, (err) => {
        if (err) {
            return res.status(500).send('디렉토리 생성 중 오류 발생');
        }

        const finalFilePath = path.join(dirPath, profileImage.name);

        // 프로필 이미지를 저장
        profileImage.mv(finalFilePath, (err) => {
            if (err) {
                return res.status(500).send('파일 저장 중 오류 발생');
            }
            res.status(200).send('프로필 이미지 업로드 성공');
        });
    });
});

// 폴더 삭제 API
app.delete('/api/deleteEpisodeFolder', (req, res) => {
    const aId = req.query.aId;
    const episodeCount = req.query.episodeCount;
    const folderPath = path.join(__dirname, '..', 'client', 'public', 'assets', 'webtoonImage', `artwork_id${aId}`, `episode_count${episodeCount}`);

    fs.rm(folderPath, { recursive: true, force: true }, (err) => {
        if (err) {
            console.error('폴더 삭제 중 오류 발생:', err);
            return res.status(500).json({ message: '폴더 삭제 실패' });
        }
        res.json({ message: '폴더 삭제 성공' });
    });
});

app.delete('/api/deleteFolder', (req, res) => {
    const artworkId = req.query.artworkId; // 요청에서 artworkId를 가져옵니다.
    const folderPath = path.join(__dirname, '..', 'client', 'public', 'assets', 'webtoonImage', `artwork_id${artworkId}`);

    fs.rm(folderPath, { recursive: true, force: true }, (err) => {
        if (err) {
            console.error('폴더 삭제 중 오류 발생:', err);
            return res.status(500).json({ message: '폴더 삭제 실패' });
        }
        res.json({ message: '폴더 삭제 성공' });
    });
});

// 이메일 전송 API
app.post('/api/sendAlertEmail', (req, res) => {
    const { email } = req.body;

    const mailOptions = {
        from: process.env.NAVER_EMAIL,
        to: email,
        subject: '작품이 가이드라인을 위반하여 거부 조치 되었습니다.',
        text: '작가님의 작품이 가이드라인을 위반하여 거부 조치 되었습니다. 원고 검토 후 다시 제출하여주시기 바랍니다.'
    };

    transporter.sendMail(mailOptions, (error, info) => {
        if (error) {
            console.error('Error sending email:', error);
            return res.status(500).json({ message: '이메일 전송에 실패하였습니다.' });
        }
        console.log('Email sent:', info.response);
        return res.status(200).json({ message: '이메일이 성공적으로 전송되었습니다.' });
    });
});

app.listen(port, () => {
    console.log(`서버가 http://localhost:${port}에서 실행 중입니다.`);
});